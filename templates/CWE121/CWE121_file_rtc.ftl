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

#define LOGFILE "/tmp/file.txt"

#undef SRC_STRING
#define SRC_STRING "TESTE"

static void test_juliet_rtc(void **state)
{
    (void)state; //unused variable

    int buf;

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
        cmocka_unit_test(test_juliet_rtc)};

    int count_fail_tests =
        cmocka_run_group_tests(tests, setup, teardown);

    return count_fail_tests;
}