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
int __wrap_globalReturnsTrueOrFalse() 
{
    //return (rand() % 2);
    return 1;
}
static void test_juliet_ft(void **state)
{
    (void)state; //unused variable

    char * data;
    char dataBuffer[100] = "";
    data = dataBuffer;
    size_t dataLen = strlen(data);
    fgets(data+dataLen, (int)(100-dataLen), stdin);

	char *bline;
    if ((bline = strchr(data, '\n')) != NULL){
        *bline = '\0';
    }

    FILE *txtFile;
    txtFile = fopen(LOGFILE, "w");
    if (txtFile != NULL){
        fprintf(txtFile, "%s", data);
        fclose(txtFile);
    }

    FILE *fileAddress;
    fileAddress = fopen("log_afl_${type}.txt", "a");
    if (fileAddress != NULL){
        fprintf(fileAddress, "%s\n", data);
        fclose(fileAddress);
    }

    char buf[100];

    freopen("/dev/null", "a", stdout);
    setbuf(stdout, buf);

    ${testedFunction}();
    
    freopen("/dev/tty", "a", stdout);
    
    char *pos;
    if ((pos = strchr(buf, '\n')) != NULL){
        *pos = '\0';
    }
    
    assert_string_equal(buf, data);
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