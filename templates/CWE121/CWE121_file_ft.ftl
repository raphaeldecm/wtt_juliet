#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdarg.h>
#include <setjmp.h>
#include <cmocka.h>

#include "${pathDataSet}${fileName}"
#include "/home/raphael/DOCFILES/DoctoralFiles/Juliet/C/testcasesupport/io.c"

#define LOGFILE "/tmp/file.txt"

static void test_juliet_ft(void **state)
{
    (void)state; //unused variable

    int buf;
    char * data;
    char dataBuffer[100] = "";
    data = dataBuffer;
    size_t dataLen = strlen(data);
    fgets(data+dataLen, (int)(100-dataLen), stdin);

	char *bline;
    if ((bline = strchr(data, '\n')) != NULL){
        *bline = '\0';
    }

    FILE *fileAddress;
    fileAddress = fopen("log_afl_${type}.txt", "a");
    if (fileAddress != NULL){
        fprintf(fileAddress, "%s\n", data);
        fclose(fileAddress);
    }
    
    ${testedFunction}();

    assert_int_equal(0, 0);
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
        cmocka_unit_test(test_juliet_ft)};

    int count_fail_tests =
        cmocka_run_group_tests(tests, setup, teardown);

    return count_fail_tests;
}