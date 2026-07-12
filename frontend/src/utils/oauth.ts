import { LocalStorageKey } from '@/constants/storage';

const generateOAuthState = () => {
    const bytes = new Uint8Array(32);
    crypto.getRandomValues(bytes);
    return Array.from(bytes, byte => byte.toString(16).padStart(2, '0')).join('');
};

const readGithubOAuthStates = (): string[] => {
    const rawStates = localStorage.getItem(LocalStorageKey.GITHUB_OAUTH_STATES);
    if (!rawStates) {
        return [];
    }
    return JSON.parse(rawStates) as string[];
};

const writeGithubOAuthStates = (states: string[]) => {
    localStorage.setItem(LocalStorageKey.GITHUB_OAUTH_STATES, JSON.stringify(states));
};

export const createGithubOAuthState = () => {
    const states = readGithubOAuthStates();
    const state = generateOAuthState();

    states.push(state);
    writeGithubOAuthStates(states);

    return state;
};

export const consumeGithubOAuthState = (state: string) => {
    const states = readGithubOAuthStates();
    const index = states.indexOf(state);
    if (index === -1) {
        return false;
    }
    states.splice(index, 1);
    writeGithubOAuthStates(states);
    return true;
};
