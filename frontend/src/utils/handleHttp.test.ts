import { expect, test, describe } from 'vitest'
import { getSingleParam } from '@/utils/handleHttp'

describe('getSingleParam', () => {
    test('测试输入为字符串的情况', () => {
        const input = 'hello';
        const result = getSingleParam(input);
        expect(result).toBe('hello');
    });

    test('测试输入为字符串数组的情况', () => {
        const input = ['hello', 'world'];
        const result = getSingleParam(input);
        expect(result).toBe('hello');
    });
});