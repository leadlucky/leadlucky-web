<template>
  <div class="container homepage-container">
    <div class="home-nav row">
      <div class="home-nav-logo col-3"><br/></div>
    </div>
    <div class="row banner-image">
      <div class="banner-content">
        <h1>Homepage</h1>
        <h4>Hey. You should buy our shit. </h4>
      </div>
    </div>

  </div>
</template>
<script>
  export default {
    name: "home"
  }
</script>
<style scoped="true" lang="css">
  body {
    margin: 0 !important;
  }





  .homepage-container {
    margin-left: 0;
    margin-right: 0;
    max-width: none;
  }

  .home-nav {
    position: fixed;

  }

  .home-nav-logo {
    background-image: url('/static/images/logo.png');
  }

  .banner-image {
    background-size: cover;
    background-image: url('/static/images/banner.jpg');
    min-height: 450px;
  }

  .banner-content {
    color: white;
    background-color: #888888aa;
    padding: 20px;
    margin: 10vh 16vw auto auto;

    animation: slideInFromLeft 700ms;
  }

  @keyframes slideInFromLeft {
    from {
      margin-right: -1000px;
    }
    to {
      margin-right: 16vw;
    }
  }

</style>
