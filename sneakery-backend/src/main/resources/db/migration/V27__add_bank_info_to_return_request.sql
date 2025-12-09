ALTER TABLE Return_Requests
    ADD return_method VARCHAR(50) NOT NULL DEFAULT 'refund';

ALTER TABLE Return_Requests
    ADD bank_name NVARCHAR(255) NULL;

ALTER TABLE Return_Requests
    ADD bank_account_number NVARCHAR(255) NULL;

ALTER TABLE Return_Requests
    ADD bank_account_holder NVARCHAR(255) NULL;