const repoName = "MorningstarPicRepo";

export interface ImageLinkGenerator {
    sourceName: string;

    generate(ownerName: string, filePath: string): string;
}

export class GithubImageLinkGenerator implements ImageLinkGenerator {
    sourceName = "Github";

    generate(ownerName: string, filePath: string): string {
        return `https://github.com/${ownerName}/${repoName}/raw/main/${filePath}`;
    }
}

export class JsDelivrImageLinkGenerator implements ImageLinkGenerator {
    sourceName = "JsDelivr";

    generate(ownerName: string, filePath: string): string {
        return `https://cdn.jsdelivr.net/gh/${ownerName}/${repoName}@main/${filePath}`;
    }
}

export class JSDMirrorImageLinkGenerator implements ImageLinkGenerator {
    sourceName = "JSDMirror";

    generate(ownerName: string, filePath: string): string {
        return `https://cdn.jsdmirror.com/gh/${ownerName}/${repoName}@main/${filePath}`;
    }
}

export class RelayImageLinkGenerator implements ImageLinkGenerator {
    sourceName = "本站中继";

    generate(ownerName: string, filePath: string): string {
        const currentHost = window.location.origin;
        return `${currentHost}/api/pic/resource/relay/${ownerName}/${filePath}`;
    }
}

export class BestImageLinkGenerator implements ImageLinkGenerator {
    sourceName = "Best";
    private imageLinkGenerator: ImageLinkGenerator;

    constructor() {
        this.imageLinkGenerator = new JsDelivrImageLinkGenerator();
    }

    generate(ownerName: string, filePath: string): string {
        return this.imageLinkGenerator.generate(ownerName, filePath);
    }

    setImageLinkGenerator(imageLinkGenerator: ImageLinkGenerator) {
        this.imageLinkGenerator = imageLinkGenerator;
    }

    getImageLinkGenerator() {
        return this.imageLinkGenerator;
    }
}