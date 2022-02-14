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
#define SNPRINTF snprintf
//Mocked functions
static void badVaSink(char * data, ...)
{
    {
        va_list args;
        va_start(args, data);
        /* POTENTIAL FLAW: Do not specify the format allowing a possible format string vulnerability */
        vfprintf(stdout, data, args);
        va_end(args);
    }
}

static void goodB2GVaSink(char * data, ...)
{
    {
        va_list args;
        va_start(args, data);
        /* FIX: Specify the format disallowing a format string vulnerability */
        vfprintf(stdout, "%s", args);
        va_end(args);
    }
}

${externVar}
${mockedFunctions}

int __wrap_globalReturnsTrueOrFalse() 
{
    //return (rand() % 2);
    return 1;
}

#define CHAR_ARRAY_SIZE 20

char inputBuffer[CHAR_ARRAY_SIZE] = "";

static void test_juliet_ft(void **state)
{
    (void)state; //unused variable

    char data[100] = "";
    
    scanf("%s\n", &data);

    char * str;
    char strBuffer[100] = "";
    str = strBuffer;

    strcpy(str, data);

	<#--  char *bline;
    if ((bline = strchr(data, '\n')) != NULL){
        *bline = '\0';
    }  -->

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

    char buf[BUFSIZ];
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