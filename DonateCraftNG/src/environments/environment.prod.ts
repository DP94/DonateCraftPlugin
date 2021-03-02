export const environment = {
  production: true,
  apiUrl: '/',
  fullAPIUrl: 'http://localhost:8000/',
  // Until we're completely happy with the flow and has been tested enough
  // Prod file will still reference staging. Can change once we're actively playing
  justGivingAPIUrl: 'https://api.staging.justgiving.com/redacted/v1',
  justGivingDonateUrl: 'https://link.staging.justgiving.com/v1/charity/donate/charityId/'
};
