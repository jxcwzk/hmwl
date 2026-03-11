const DEFAULT_PRICING_CONFIG = {
  basePrice: 10,
  pricePerKg: 1.5,
  pricePerVolume: 80,
  pricePerKm: 0.5,
  minPrice: 20,
  trunkRouteDiscount: 0.1,
  distanceThresholds: {
    short: 100,
    medium: 500,
    long: 1000
  },
  volumeThresholds: {
    small: 0.1,
    medium: 1,
    large: 5
  },
  weightThresholds: {
    light: 5,
    medium: 20,
    heavy: 100
  }
};

function calculateLogisticsPrice(params) {
  const {
    weight = 0,
    volume = 0,
    distance = 0,
    orderType = 0,
    startNetworkId = null,
    endNetworkId = null
  } = params;

  const config = DEFAULT_PRICING_CONFIG;

  let baseFee = config.basePrice;

  let weightFee = 0;
  if (weight > 0) {
    if (weight <= config.weightThresholds.light) {
      weightFee = weight * config.pricePerKg * 0.8;
    } else if (weight <= config.weightThresholds.medium) {
      weightFee = weight * config.pricePerKg;
    } else if (weight <= config.weightThresholds.heavy) {
      weightFee = weight * config.pricePerKg * 1.2;
    } else {
      weightFee = weight * config.pricePerKg * 1.5;
    }
  }

  let volumeFee = 0;
  if (volume > 0) {
    if (volume <= config.volumeThresholds.small) {
      volumeFee = volume * config.pricePerVolume * 0.8;
    } else if (volume <= config.volumeThresholds.medium) {
      volumeFee = volume * config.pricePerVolume;
    } else if (volume <= config.volumeThresholds.large) {
      volumeFee = volume * config.pricePerVolume * 1.2;
    } else {
      volumeFee = volume * config.pricePerVolume * 1.5;
    }
  }

  let distanceFee = 0;
  if (distance > 0) {
    if (distance <= config.distanceThresholds.short) {
      distanceFee = distance * config.pricePerKm * 1.2;
    } else if (distance <= config.distanceThresholds.medium) {
      distanceFee = distance * config.pricePerKm;
    } else if (distance <= config.distanceThresholds.long) {
      distanceFee = distance * config.pricePerKm * 0.8;
    } else {
      distanceFee = distance * config.pricePerKm * 0.6;
    }
  }

  let totalFee = baseFee + weightFee + volumeFee + distanceFee;

  if (orderType === 1) {
    totalFee = totalFee * 1.5;
  }

  if (totalFee < config.minPrice) {
    totalFee = config.minPrice;
  }

  totalFee = Math.round(totalFee * 100) / 100;

  return {
    totalFee,
    breakdown: {
      baseFee: Math.round(baseFee * 100) / 100,
      weightFee: Math.round(weightFee * 100) / 100,
      volumeFee: Math.round(volumeFee * 100) / 100,
      distanceFee: Math.round(distanceFee * 100) / 100
    },
    details: {
      weight,
      volume,
      distance,
      orderType: orderType === 0 ? '零担' : '整车',
      pricingLevel: getPricingLevel(weight, volume, distance)
    }
  };
}

function getPricingLevel(weight, volume, distance) {
  const config = DEFAULT_PRICING_CONFIG;
  let level = 'standard';

  if (weight > config.weightThresholds.heavy ||
      volume > config.volumeThresholds.large ||
      distance > config.distanceThresholds.long) {
    level = 'premium';
  } else if (weight <= config.weightThresholds.light &&
             volume <= config.volumeThresholds.small &&
             distance <= config.distanceThresholds.short) {
    level = 'economy';
  }

  return level;
}

function estimateDistance(startNetworkId, endNetworkId) {
  const mockDistances = {
    '1-2': 150,
    '1-3': 300,
    '2-3': 200,
    '2-1': 150,
    '3-1': 300,
    '3-2': 200
  };

  const key = `${startNetworkId}-${endNetworkId}`;
  return mockDistances[key] || 100;
}

module.exports = {
  calculateLogisticsPrice,
  estimateDistance,
  DEFAULT_PRICING_CONFIG
};
