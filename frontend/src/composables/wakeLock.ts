import { onMounted, onUnmounted, readonly, ref } from 'vue';

export const useWakeLock = () => {
    const isSupported = 'wakeLock' in navigator;
    let wakeLock: WakeLockSentinel | null = null;
    const _shouldActive = ref(false);

    const request = async () => {
        if (!isSupported || wakeLock || document.visibilityState !== 'visible') return;
        try {
            wakeLock = await navigator.wakeLock.request('screen');
            console.log('🚀【WakeLock】成功向手机系统拿到屏幕锁！屏幕将保持常亮');
            wakeLock.addEventListener('release', () => {
                // 切后台或系统断开
                wakeLock = null;
                console.log('🍂【WakeLock】手机底层锁已物理释放（可能由于切后台或锁屏）');
            });
        } catch (err) {
            console.error('Wake Lock 激活失败:', err);
            _shouldActive.value = false;
        }
    };

    const release = async () => {
        if (wakeLock) {
            await wakeLock.release();
            console.log('🛑【WakeLock】用户主动释放了屏幕锁');
        }
        wakeLock = null;
    };

    const handleVisibilityChange = async () => {
        // 如果用户意愿是开启的，且回到了前台，且当前锁空了，自动续期
        if (_shouldActive.value && document.visibilityState === 'visible' && !wakeLock) {
            await request();
        }
    };
    onMounted(() => {
        document.addEventListener('visibilitychange', handleVisibilityChange);
    });
    onUnmounted(() => {
        document.removeEventListener('visibilitychange', handleVisibilityChange);
        release().then(() => {});
    });

    const enableScreenWake = async () => {
        if (!isSupported) return;
        _shouldActive.value = true;
        if (document.visibilityState === 'visible') {
            await request();
        }
    };

    const disableScreenWake = async () => {
        _shouldActive.value = false;
        await release();
    };

    return {
        isSupported,
        shouldActive: readonly(_shouldActive),
        enableScreenWake,
        disableScreenWake,
    };
};
