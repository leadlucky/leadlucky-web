const store =  {
  userData: {
    user: null,
    isPremium(){
      return this.user && this.user.premiumStatus === "active"
    }
  },
  portalData: {
    darkTheme: localStorage.getItem('darkTheme') === 'true',
    fullscreen: false,
    miniSidebar: false
  }
};

export default store;
