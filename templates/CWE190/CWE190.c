#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdarg.h>
#include <setjmp.h>
#include <cmocka.h>

#include "/home/raphael/DOCFILES/DoctoralFiles/Juliet/C1.2/testcases/CWE190_Integer_Overflow/s01/CWE190_Integer_Overflow__char_fscanf_add_01.c"
#include "/home/raphael/DOCFILES/DoctoralFiles/Juliet/C1.2/testcasesupport/io.c"

#define CHAR_ARRAY_SIZE 20

char inputBuffer[BUFSIZ];

//Mocked functions

#define stdin 1

void __wrap_fscanf(FILE *__restrict __stream,
		   const char *__restrict __format, ...)
{
    //strcpy(__arg, inputBuffer);
    printf("FSCANF");
    // return 1;
}

static void test_juliet_rtc(void **state)
{
    (void)state; //unused variable
    char data[BUFSIZ] = "muniz";
    //fscanf (stdin, "%c", &data);

    // sprintf(inputBuffer, "%s", data);
    // printf("%02x\n", data);
    // char buf[BUFSIZ];
    // freopen("/dev/null", "a", stdout);
    // setbuf(stdout, buf);

    CWE190_Integer_Overflow__char_fscanf_add_01_bad();
    
    // freopen("/dev/tty", "a", stdout);

    // int bufI = atoi(buf);
    assert_true(1 > 0);
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