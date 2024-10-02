import {describe, expect, test} from 'vitest'
import {getFirstParam} from '@/utils/handleHttp'

describe('getFirstParam', () => {
    test('测试输入为字符串的情况', () => {
        const input = 'hello';
        const result = getFirstParam(input);
        expect(result).toBe('hello');
    });

    test('测试输入为字符串数组的情况', () => {
        const input = ['hello', 'world'];
        const result = getFirstParam(input);
        expect(result).toBe('hello');
    });
});