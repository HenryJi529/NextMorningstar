import type {GeolocationCoords} from "@/types/auth";
import type {AMapGeolocationResponse} from "@/types/amap";
import axios from '@/axios/index';
import Bowser from "bowser";
import MobileDetect from "mobile-detect";

export const getClientGeolocationCoords = async () => {
    if (import.meta.env.DEV) {
        return {
            latitude: 31.099,
            longitude: 117.737
        }
    }

    const response: AMapGeolocationResponse = (await axios.get(`https://restapi.amap.com/v3/ip?key=${import.meta.env.VITE_AMAP_KEY}`)).data;
    const locs = response.rectangle.split(';');
    const coords: GeolocationCoords = {
        latitude: Math.round((Number(locs[0].split(',')[1]) + Number(locs[1].split(',')[1])) / 2 * 1000) / 1000,
        longitude: Math.round((Number(locs[0].split(',')[0]) + Number(locs[1].split(',')[0])) / 2 * 1000) / 1000,
    };
    return coords;
}

const getClientInfo = () => {
    // @ts-ignore 忽略 userAgentData 类型提示
    return Bowser.parse(window.navigator.userAgent, navigator.userAgentData);
}

export const isMobile = () => getClientInfo().platform.type === "mobile";

export const isDesktop = () => getClientInfo().platform.type === "desktop";

export const isBot = () => new MobileDetect(navigator.userAgent).is('bot');

