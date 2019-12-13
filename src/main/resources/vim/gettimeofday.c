#if defined(HAVE_GETTIMEOFDAY) && defined(HAVE_SYS_TIME_H)
    /* Remember at what time we started, so that we know how much longer we
    * should wait after being interrupted. */
#define USE_START_TV
    struct timeval  start_tv;

    if (msec > 0 && (
#ifdef FEAT_XCLIPBOARD
        xterm_Shell != (Widget)0
#if defined(USE_XSMP) || defined(FEAT_MZSCHEME)
        ||
#endif
#endif
#ifdef USE_XSMP
        xsmp_icefd != -1
#ifdef FEAT_MZSCHEME
        ||
#endif
#endif
#ifdef FEAT_MZSCHEME
    (mzthreads_allowed() && p_mzq > 0)
#endif
        ))
    gettimeofday(&start_tv, NULL);
#endif
