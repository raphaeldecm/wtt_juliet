#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdarg.h>
#include <setjmp.h>
#include <cmocka.h>

#include "${pathDataSet}${fileName}"
#include "/home/raphael/DOCFILES/DoctoralFiles/Juliet/C1.2/testcasesupport/io.c"

#define CHAR_ARRAY_SIZE 20

int inputBuffer;

//Mocked functions
${externVar}
${mockedFunctions}

static void test_juliet_rtc(void **state)
{
    (void)state; //unused variable

    int64_t data;
    data = 0LL;
    scanf("%lld\n", &data);

    int64_t result = data * 2;
    
    char charRes[CHAR_MAX + 1];
    sprintf(charRes, "%lld", result);

    FILE *inputFile;
    inputFile = fopen("read_ft.txt", "w");
    if (inputFile != NULL){
        fprintf(inputFile, "%lld\n", data);
        fclose(inputFile);
    }

    FILE *fileAddress;
    fileAddress = fopen("log_afl_${type}.txt", "a");
    if (fileAddress != NULL){
        fprintf(fileAddress, "%lld\n", data);
        fclose(fileAddress);
    }

    char buf[BUFSIZ];
    freopen("/dev/null", "a", stdout);
    setbuf(stdout, buf);
    
    freopen("read_ft.txt", "r", stdin);
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