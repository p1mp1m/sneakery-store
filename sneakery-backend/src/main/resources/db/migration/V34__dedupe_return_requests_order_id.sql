/* ============================================================
   V35__dedupe_return_requests_order_id.sql
   SQL Server - Dedupe return_requests by order_id
   Keep newest by created_at DESC, then id DESC
   Update all referencing FK rows -> keep_id
   Delete duplicates
   Add UNIQUE constraint on order_id
   ============================================================ */

SET NOCOUNT ON;
SET XACT_ABORT ON;

BEGIN TRY
BEGIN TRAN;

    ----------------------------------------------------------------
    -- 1) Rank rows per order_id and build duplicate -> keep mapping
    ----------------------------------------------------------------
    IF OBJECT_ID('tempdb..#rr_dups') IS NOT NULL DROP TABLE #rr_dups;

;WITH ranked AS (
    SELECT
        rr.id,
        rr.order_id,
        rr.created_at,
        ROW_NUMBER() OVER (
                PARTITION BY rr.order_id
                ORDER BY rr.created_at DESC, rr.id DESC
            ) AS rn
    FROM dbo.return_requests rr
    WHERE rr.order_id IS NOT NULL
)
 SELECT
     d.order_id,
     d.id  AS dup_id,
     k.id  AS keep_id
 INTO #rr_dups
 FROM ranked d
          JOIN ranked k
               ON k.order_id = d.order_id
                   AND k.rn = 1
 WHERE d.rn > 1;

DECLARE @dupCount INT = (SELECT COUNT(*) FROM #rr_dups);
    PRINT CONCAT('[Flyway] return_requests duplicates found: ', @dupCount);

    ----------------------------------------------------------------
    -- 2) Re-point ALL foreign keys referencing dbo.return_requests(id)
    --    from dup_id -> keep_id (dynamic)
    ----------------------------------------------------------------
    IF @dupCount > 0
BEGIN
        DECLARE @schemaName SYSNAME;
        DECLARE @tableName  SYSNAME;
        DECLARE @colName    SYSNAME;
        DECLARE @sql        NVARCHAR(MAX);

        DECLARE fk_cursor CURSOR FAST_FORWARD FOR
SELECT
    sch.name AS schema_name,
    t.name   AS table_name,
    c.name   AS column_name
FROM sys.foreign_key_columns fkc
         JOIN sys.tables rt              ON rt.object_id = fkc.referenced_object_id
         JOIN sys.columns rc             ON rc.object_id = rt.object_id AND rc.column_id = fkc.referenced_column_id
         JOIN sys.tables t               ON t.object_id  = fkc.parent_object_id
         JOIN sys.columns c              ON c.object_id  = t.object_id  AND c.column_id = fkc.parent_column_id
         JOIN sys.schemas sch            ON sch.schema_id = t.schema_id
WHERE rt.object_id = OBJECT_ID('dbo.return_requests')
  AND rc.name = 'id';

OPEN fk_cursor;
FETCH NEXT FROM fk_cursor INTO @schemaName, @tableName, @colName;

WHILE @@FETCH_STATUS = 0
BEGIN
            SET @sql = N'
                UPDATE tgt
                SET tgt.' + QUOTENAME(@colName) + N' = d.keep_id
                FROM ' + QUOTENAME(@schemaName) + N'.' + QUOTENAME(@tableName) + N' tgt
                JOIN #rr_dups d
                  ON tgt.' + QUOTENAME(@colName) + N' = d.dup_id;
            ';

            PRINT CONCAT('[Flyway] Updating FK ', @schemaName, '.', @tableName, '.', @colName);
EXEC sp_executesql @sql;

FETCH NEXT FROM fk_cursor INTO @schemaName, @tableName, @colName;
END

CLOSE fk_cursor;
DEALLOCATE fk_cursor;

        ----------------------------------------------------------------
        -- 3) Delete duplicate rows
        ----------------------------------------------------------------
        PRINT '[Flyway] Deleting duplicate return_requests rows...';

        DELETE rr
        FROM dbo.return_requests rr
        JOIN #rr_dups d ON d.dup_id = rr.id;

        PRINT CONCAT('[Flyway] Deleted duplicates: ', @@ROWCOUNT);
END

    ----------------------------------------------------------------
    -- 4) Add UNIQUE constraint on order_id (prevents recurrence)
    ----------------------------------------------------------------
    IF NOT EXISTS (
        SELECT 1
        FROM sys.key_constraints kc
        WHERE kc.[type] = 'UQ'
          AND kc.parent_object_id = OBJECT_ID('dbo.return_requests')
          AND kc.name = 'UQ_return_requests_order_id'
    )
BEGIN
        PRINT '[Flyway] Adding unique constraint UQ_return_requests_order_id...';
ALTER TABLE dbo.return_requests
    ADD CONSTRAINT UQ_return_requests_order_id UNIQUE (order_id);
END
ELSE
BEGIN
        PRINT '[Flyway] Unique constraint already exists.';
END

COMMIT TRAN;
PRINT '[Flyway] Done.';
END TRY
BEGIN CATCH
IF @@TRANCOUNT > 0 ROLLBACK TRAN;

    DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @ErrSev INT = ERROR_SEVERITY();
    DECLARE @ErrState INT = ERROR_STATE();

    PRINT CONCAT('[Flyway] FAILED: ', @ErrMsg);
    RAISERROR(@ErrMsg, @ErrSev, @ErrState);
END CATCH;
