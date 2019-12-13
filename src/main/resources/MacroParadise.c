//#include <stdio.h>
//#include <stdlib.h>

// FeatureModel Constraints
// B => A
// !A or !C

//#define FEATURE_A
//#define FEATURE_B
//#define FEATURE_C

char* computeMessage();

#ifdef FEATURE_A
char* computeMessage() {
    int buflen = 2;
#ifdef FEATURE_B
    ++buflen;
#endif
    char* message = malloc(buflen);
    message[0] = 'A';
#ifdef FEATURE_B
    message[1] = 'B';
#endif
    message[buflen - 1] = 0;
    return message;
}
#elif defined(FEATURE_C)
char* computeMessage() {
    char* message = malloc(2);
    message[0] = 'C';
    message[1] = 0;
    return message;
}
#else
char* computeMessage() {
    char* message = malloc(2);
    message[0] = 'N';
    message[1] = 0;
    return message;
}
#endif

int main(void) {
    char * message = computeMessage();
    printf("%s", message);
    free(message);
}