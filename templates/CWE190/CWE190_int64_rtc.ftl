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

static void test_juliet_rtc(void **state)
{
    (void)state; //unused variable
    int64_t data;
    data = LLONG_MAX;
    int64_t result = data * 2;
    
    char charRes[CHAR_MAX];
    sprintf(charRes, "%lld", result);
    
    FILE *inputFile;
    inputFile = fopen("read_rtc.txt", "w");
    if (inputFile != NULL){
        fprintf(inputFile, "%lld\n", data);
        fclose(inputFile);
    }

    char buf[BUFSIZ];
    freopen("/dev/null", "a", stdout);
    setbuf(stdout, buf);

    freopen("read_rtc.txt", "r", stdin); // change the behaviour of standard input to read from a file.
    CWE190_Integer_Overflow__int64_t_fscanf_multiply_67_structType myStruct;
myStruct.structFirst = data;
    ${testedFunction}();

    freopen("/dev/tty", "a", stdout);
    
    char *pos;
    if ((pos = strchr(buf, '\n')) != NULL)
    {
        *pos = '\0';
    }
    if(strcmp(buf, "data value is too large to perform arithmetic safely.") == 0){
        assert_string_equal(buf, "data value is too large to perform arithmetic safely.");
    } else if(data > 0) {
        ${assert}
    } else {
        assert_true(1);
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