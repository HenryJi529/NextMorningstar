export const roundToDecimal = (num: number, decimalPlaces: number) => {
    const factor = 10 ** decimalPlaces;
    return Math.round(num * factor) / factor;
}