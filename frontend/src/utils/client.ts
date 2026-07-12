import type { GeolocationCoords } from '@/types/auth';
import type { AMapGeolocationResponse } from '@/types/amap';
import { LocalStorageKey } from '@/constants/storage';
import axios from 'axios';
import Bowser from 'bowser';
import MobileDetect from 'mobile-detect';

const getClientGeolocationCoordsByBrowser = (): Promise<GeolocationCoords> => {
    return new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(
            pos => {
                const coords: GeolocationCoords = {
                    latitude: pos.coords.latitude,
                    longitude: pos.coords.longitude,
                };
                return resolve(coords);
            },
            err => reject(err)
        );
    });
};

const getClientGeolocationCoordsByAMap = async () => {
    const response: AMapGeolocationResponse = (
        await axios.get(`https://restapi.amap.com/v3/ip?key=${import.meta.env.VITE_AMAP_KEY}`)
    ).data;
    const locs = response.rectangle.split(';');
    const coords: GeolocationCoords = {
        latitude:
            Math.round(
                ((Number(locs[0].split(',')[1]) + Number(locs[1].split(',')[1])) / 2) * 1000
            ) / 1000,
        longitude:
            Math.round(
                ((Number(locs[0].split(',')[0]) + Number(locs[1].split(',')[0])) / 2) * 1000
            ) / 1000,
    };
    return coords;
};

export const getClientGeolocationCoords = async () => {
    try {
        return await getClientGeolocationCoordsByBrowser();
    } catch (e) {
        return await getClientGeolocationCoordsByAMap();
    }
};

export const getClientDeviceId = () => {
    let deviceId = localStorage.getItem(LocalStorageKey.DEVICE_ID);
    if (!deviceId) {
        deviceId = crypto.randomUUID();
        localStorage.setItem(LocalStorageKey.DEVICE_ID, deviceId);
    }
    return deviceId;
};

const getClientDeviceInfo = () => {
    // @ts-expect-error 忽略 userAgentData 类型提示
    return Bowser.parse(window.navigator.userAgent, navigator.userAgentData);
};

export const isMobile = () => getClientDeviceInfo().platform.type === 'mobile';

export const isDesktop = () => getClientDeviceInfo().platform.type === 'desktop';

export const isBot = () => new MobileDetect(navigator.userAgent).is('bot');
