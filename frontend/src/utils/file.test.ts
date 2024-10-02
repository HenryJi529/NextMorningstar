import { getFileExtension } from '@/utils/file';
import { describe, expect, test } from 'vitest';

describe('getFileExtension', () => {
    test('test.jpg', () => {
        const filename = 'test.jpg';
        const result = getFileExtension(new File([new Uint8Array(0)], filename));
        expect(result).toBe('jpg');
    });

    test('.gitignore', () => {
        const filename = '.gitignore';
        const result = getFileExtension(new File([new Uint8Array(0)], filename));
        expect(result).toBe('');
    });

    test('image.tar.gz', () => {
        const filename = 'image.tar.gz';
        const result = getFileExtension(new File([new Uint8Array(0)], filename));
        expect(result).toBe('gz');
    });

    test('document.', () => {
        const filename = 'document.';
        const result = getFileExtension(new File([new Uint8Array(0)], filename));
        expect(result).toBe('');
    });
});
