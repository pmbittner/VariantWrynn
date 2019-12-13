#if SDSORT_USES_RAM

  // If using dynamic ram for names, allocate on the heap.
  #if SDSORT_CACHE_NAMES
    char sortshort[SDSORT_LIMIT][FILENAME_LENGTH];
    char sortnames[SDSORT_LIMIT][FILENAME_LENGTH];
  #elif !SDSORT_USES_STACK
    char sortnames[SDSORT_LIMIT][FILENAME_LENGTH];
  #endif

  // Folder sorting uses an isDir array when caching items.
  #if HAS_FOLDER_SORTING  && (SDSORT_CACHE_NAMES || !SDSORT_USES_STACK)
    uint8_t isDir[(SDSORT_LIMIT+7)>>3];
  #endif

#endif // SDSORT_USES_RAM
