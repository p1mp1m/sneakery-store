/**
 * Composable for filter presets management
 * Handles saving, loading, and managing filter presets
 * 
 * @example
 * ```javascript
 * const { presets, savePreset, applyPreset, history, addToHistory } = useFilterPresets('products');
 * 
 * // Save current filters as preset
 * savePreset('My Preset', currentFilters);
 * 
 * // Apply a preset
 * const filters = applyPreset(presetId);
 * 
 * // Add to history
 * addToHistory(currentFilters);
 * ```
 * 
 * @param {string} filterType - Type of filter (e.g., 'products', 'orders') for namespacing
 * 
 * @returns {Object} Filter preset utilities
 * @returns {Ref<Array>} returns.presets - Saved filter presets (reactive)
 * @returns {Ref<Array>} returns.history - Filter history (reactive)
 * @returns {Ref<string>} returns.currentPresetName - Current preset name (reactive)
 * @returns {Function} returns.loadPresets - Load presets from storage
 * @returns {Function} returns.savePreset - Save current filters as preset
 * @returns {Function} returns.deletePreset - Delete a preset
 * @returns {Function} returns.applyPreset - Apply a preset
 * @returns {Function} returns.addToHistory - Add filters to history
 * @returns {Function} returns.applyHistory - Apply filters from history
 * @returns {Function} returns.clearHistory - Clear filter history
 * @returns {Function} returns.downloadFilters - Export filters to JSON file
 * @returns {Function} returns.uploadFilters - Import filters from JSON file
 */

import { ref, onMounted } from 'vue';
import { useLocalStorageCache } from './useLocalStorageCache';

const PRESETS_STORAGE_KEY = 'admin_filter_presets';
const FILTER_HISTORY_KEY = 'admin_filter_history';
const MAX_HISTORY_ITEMS = 10;

export function useFilterPresets(filterType = 'products') {
  const { getCache, setCache } = useLocalStorageCache();
  const presets = ref([]);
  const history = ref([]);
  const currentPresetName = ref('');

  /**
   * Load presets from localStorage
   */
  const loadPresets = () => {
    try {
      const cached = getCache(`${PRESETS_STORAGE_KEY}_${filterType}`);
      if (cached) {
        presets.value = cached;
      }
    } catch (error) {
      console.error('Error loading presets:', error);
      presets.value = [];
    }
  };

  /**
   * Load filter history
   */
  const loadHistory = () => {
    try {
      const cached = getCache(`${FILTER_HISTORY_KEY}_${filterType}`);
      if (cached) {
        history.value = cached;
      }
    } catch (error) {
      console.error('Error loading history:', error);
      history.value = [];
    }
  };

  /**
   * Save a filter preset
   * @param {string} name - Preset name
   * @param {Object} filters - Filter object
   */
  const savePreset = (name, filters) => {
    if (!name || !name.trim()) {
      throw new Error('Tên preset không được để trống');
    }

    const preset = {
      id: Date.now().toString(),
      name: name.trim(),
      filters: { ...filters },
      createdAt: new Date().toISOString()
    };

    // Check if preset with same name exists
    const existingIndex = presets.value.findIndex(p => p.name.toLowerCase() === name.toLowerCase());
    if (existingIndex > -1) {
      presets.value[existingIndex] = preset;
    } else {
      presets.value.push(preset);
    }

    // Save to localStorage
    setCache(`${PRESETS_STORAGE_KEY}_${filterType}`, presets.value, 365 * 24 * 60 * 60 * 1000); // 1 year
  };

  /**
   * Delete a preset
   * @param {string} presetId - Preset ID
   */
  const deletePreset = (presetId) => {
    presets.value = presets.value.filter(p => p.id !== presetId);
    setCache(`${PRESETS_STORAGE_KEY}_${filterType}`, presets.value, 365 * 24 * 60 * 60 * 1000);
  };

  /**
   * Apply a preset
   * @param {string} presetId - Preset ID
   * @returns {Object} Filter object
   */
  const applyPreset = (presetId) => {
    const preset = presets.value.find(p => p.id === presetId);
    if (preset) {
      // Add to history
      addToHistory(preset.filters);
      return { ...preset.filters };
    }
    return null;
  };

  /**
   * Add filters to history
   * @param {Object} filters - Filter object
   */
  const addToHistory = (filters) => {
    // Don't add if same as last history item
    if (history.value.length > 0) {
      const lastFilters = history.value[0].filters;
      if (JSON.stringify(lastFilters) === JSON.stringify(filters)) {
        return;
      }
    }

    const historyItem = {
      id: Date.now().toString(),
      filters: { ...filters },
      timestamp: new Date().toISOString()
    };

    history.value.unshift(historyItem);
    
    // Keep only last MAX_HISTORY_ITEMS
    if (history.value.length > MAX_HISTORY_ITEMS) {
      history.value = history.value.slice(0, MAX_HISTORY_ITEMS);
    }

    // Save to localStorage
    setCache(`${FILTER_HISTORY_KEY}_${filterType}`, history.value, 30 * 24 * 60 * 60 * 1000); // 30 days
  };

  /**
   * Apply history item
   * @param {string} historyId - History item ID
   * @returns {Object} Filter object
   */
  const applyHistory = (historyId) => {
    const historyItem = history.value.find(h => h.id === historyId);
    if (historyItem) {
      return { ...historyItem.filters };
    }
    return null;
  };

  /**
   * Clear history
   */
  const clearHistory = () => {
    history.value = [];
    setCache(`${FILTER_HISTORY_KEY}_${filterType}`, [], 30 * 24 * 60 * 60 * 1000);
  };

  /**
   * Export filters as JSON
   * @param {Object} filters - Filter object
   * @returns {string} JSON string
   */
  const exportFilters = (filters) => {
    const exportData = {
      version: '1.0',
      filterType,
      filters: { ...filters },
      exportedAt: new Date().toISOString()
    };
    return JSON.stringify(exportData, null, 2);
  };

  /**
   * Import filters from JSON
   * @param {string} jsonString - JSON string
   * @returns {Object} Filter object
   */
  const importFilters = (jsonString) => {
    try {
      const data = JSON.parse(jsonString);
      if (data.filters && typeof data.filters === 'object') {
        return { ...data.filters };
      }
      throw new Error('Định dạng file không hợp lệ');
    } catch (error) {
      throw new Error('Không thể đọc file. Vui lòng kiểm tra định dạng JSON.');
    }
  };

  /**
   * Download filters as file
   * @param {Object} filters - Filter object
   * @param {string} filename - Filename
   */
  const downloadFilters = (filters, filename = 'filters.json') => {
    const json = exportFilters(filters);
    const blob = new Blob([json], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  /**
   * Upload filters from file
   * @param {File} file - File object
   * @returns {Promise<Object>} Filter object
   */
  const uploadFilters = (file) => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e) => {
        try {
          const filters = importFilters(e.target.result);
          resolve(filters);
        } catch (error) {
          reject(error);
        }
      };
      reader.onerror = () => reject(new Error('Không thể đọc file'));
      reader.readAsText(file);
    });
  };

  onMounted(() => {
    loadPresets();
    loadHistory();
  });

  return {
    presets,
    history,
    currentPresetName,
    savePreset,
    deletePreset,
    applyPreset,
    addToHistory,
    applyHistory,
    clearHistory,
    exportFilters,
    importFilters,
    downloadFilters,
    uploadFilters
  };
}

