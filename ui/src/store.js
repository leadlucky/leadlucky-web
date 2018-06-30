const store =  {
  userData: {
    user: null
  },
  portalData: {
    darkTheme: localStorage.getItem('darkTheme') === 'true',
    fullscreen: false
  }
};

export default store;
