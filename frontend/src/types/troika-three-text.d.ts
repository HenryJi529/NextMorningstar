declare module 'troika-three-text' {
    import { Mesh } from 'three';

    export class Text extends Mesh {
        text: string;
        fontSize: number;
        color: string | number;
        anchorX: string;
        anchorY: string;
        sync(): void;
        [key: string]: string | number;
    }
}
