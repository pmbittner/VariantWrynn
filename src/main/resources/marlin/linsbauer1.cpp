// By default the sort index is static
#if SDSORT_DYNAMIC_RAM
  uint8_t *sort_order;
#else
  uint8_t sort_order[SDSORT_LIMIT];
#endif

// Cache filenames to speed up SD menus.
#if SDSORT_USES_RAM

  // If using dynamic ram for names, allocate on the heap.
  #if SDSORT_CACHE_NAMES
    #if SDSORT_DYNAMIC_RAM
      char **sortshort, **sortnames;
    #else
      char sortshort[SDSORT_LIMIT][FILENAME_LENGTH];
      char sortnames[SDSORT_LIMIT][FILENAME_LENGTH];
    #endif
  #elif !SDSORT_USES_STACK
    char sortnames[SDSORT_LIMIT][FILENAME_LENGTH];
  #endif

  #if HAS_FOLDER_SORTING
    #if SDSORT_DYNAMIC_RAM
      uint8_t *isDir;
    #elif SDSORT_CACHE_NAMES || SDSORT_USES_STACK
      uint8_t isDir[(SDSORT_LIMIT+7)>>3];
    #endif
  #endif

#endif // SDSORT_USES_RAM
