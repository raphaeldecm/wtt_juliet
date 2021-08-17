#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdarg.h>
#include <setjmp.h>
#include <cmocka.h>

//#include "/home/raphael/DOCFILES/DoctoralFiles/Juliet/C/testcases/CWE121_Stack_Based_Buffer_Overflow/s04/CWE121_Stack_Based_Buffer_Overflow__CWE805_char_declare_snprintf_02.c"
#include "${pathDataSet}${fileName}"
#include "/home/raphael/DOCFILES/DoctoralFiles/Juliet/C/testcasesupport/io.c"

#define CHAR_ARRAY_SIZE (3 * sizeof(data) + 2)

char inputBuffer[BUFSIZ];

//Mocked
${externVar}
${mockedFunctions}
char __wrap_fgets(char *__restrict __s, int __n, FILE *__restrict __stream)
{
    strcpy(__s, inputBuffer);
    return __s;
}

static void test_juliet_rtc(void **state)
{
    (void)state; //unused variable

    int data;
    data = -1;

    char input[CHAR_ARRAY_SIZE];
    scanf("%s\n", &input);

    sprintf(inputBuffer, "%s", input);

    FILE *fileAddress;
    fileAddress = fopen("log_afl_${type}.txt", "a");
    if (fileAddress != NULL){
        fprintf(fileAddress, "%s\n", input);
        fclose(fileAddress);
    }

    char buf[BUFSIZ];
    freopen("/dev/null", "a", stdout);
    setbuf(stdout, buf);

    ${testedFunction}();

    freopen("/dev/tty", "a", stdout);

    data = atoi(buf);
    int ipt = atoi(input);
    
    if (ipt >= 0 && ipt < (10)){
        assert_true(data >= 0);
    } else {
        assert_string_equal("ERROR: Array index is out-of-bounds\n", buf);
    }
}

int setup(void **state){
    return 0;
}

int teardown(void **state){
    return 0;
}

int main(int argc, char **argv)
{
    const struct CMUnitTest tests[] = {
        cmocka_unit_test(test_juliet_rtc)};

    int count_fail_tests =
        cmocka_run_group_tests(tests, setup, teardown);

    return count_fail_tests;
}