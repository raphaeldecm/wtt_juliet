#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdarg.h>
#include <setjmp.h>
#include <cmocka.h>

#include "${pathDataSet}${fileName}"
#include "/home/raphael/DOCFILES/DoctoralFiles/Juliet/C1.2/testcasesupport/io.c"

//Mocked functions
${externVar}
${mockedFunctions}

#define CHAR_ARRAY_SIZE 20

char inputBuffer[CHAR_ARRAY_SIZE] = "";

char __wrap_fgets(char *__restrict __s, int __n, FILE *__restrict __stream)
{
    strcpy(__s, inputBuffer);
    return __s;
}

static void test_juliet_rtc(void **state)
{
    (void)state; //unused variable
    int data;
    data = INT_MAX;
    int result = data + 1;

    sprintf(inputBuffer, "%d", data);

    char buf[BUFSIZ];
    freopen("/dev/null", "a", stdout);
    setbuf(stdout, buf);
    
    ${testedFunction}();

    freopen("/dev/tty", "a", stdout);
    
    char *pos;
    if ((pos = strchr(buf, '\n')) != NULL)
    {
        *pos = '\0';
    }
    if(strcmp(buf, "data value is too large to perform arithmetic safely.") == 0){
        assert_string_equal(buf, "data value is too large to perform arithmetic safely.");
    } else {
        ${assert}
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