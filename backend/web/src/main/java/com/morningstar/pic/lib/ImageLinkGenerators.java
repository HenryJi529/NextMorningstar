package com.morningstar.pic.lib;

import lombok.Getter;

@Getter
public enum ImageLinkGenerators {
    Github(new GithubImageLinkGenerator()),
    JSDMirror(new JSDMirrorImageLinkGenerator()),
    JsDelivr(new JsDelivrImageLinkGenerator()),
    Relay(new RelayImageLinkGenerator()),
    ;

    private final ImageLinkGenerator imageLinkGenerator;

    ImageLinkGenerators(ImageLinkGenerator imageLinkGenerator) {
        this.imageLinkGenerator = imageLinkGenerator;
    }
}
