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

char inputBuffer[BUFSIZ];

//Mocked functions
${externVar}
${mockedFunctions}
extern int fscanf (FILE *__restrict __stream,
		   const char *__restrict __format, ...){
    return inputBuffer;
}
char __wrap_fgets(char *__restrict __s, int __n, FILE *__restrict __stream)
{
    strcpy(__s, inputBuffer);
    return __s;
}

static void test_juliet_rtc(void **state)
{
    (void)state; //unused variable

    float data;
    data = 0.0F;
    /* Initialize Data */
    scanf("%f\n", &data);
    
    sprintf(inputBuffer, "%f", data);

    FILE *fileAddress;
    fileAddress = fopen("log_afl_${type}.txt", "a");
    if (fileAddress != NULL){
        fprintf(fileAddress, "%f\n", data);
        fclose(fileAddress);
    }

    char buf[BUFSIZ];
    freopen("/dev/null", "a", stdout);
    setbuf(stdout, buf);

    ${testedFunction}();

    freopen("/dev/tty", "a", stdout);

    if (data != 0)
    {
        assert_true(1);
    }
    else
    {
        char *pos;
        if ((pos = strchr(buf, '\n')) != NULL)
        {
            *pos = '\0';
        }
        assert_string_equal(buf ,"This would result in a divide by zero");
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