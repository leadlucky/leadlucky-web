<template>
<div>
  <ul class="nav nav-tabs customtab" role="tablist">
    <li v-for="(title, i) in titles" @click="active=i" class="nav-item">
      <a :class="`nav-link ${active == i ? 'active ': ''}show`" data-toggle="tab" href="#home2" role="tab" aria-selected="true">
        <span class="hidden-sm-up"><i class="ti-home"></i></span>
        <span class="hidden-xs-down">{{title}}</span>
      </a>
    </li>
  </ul>
  <div class="tab-content">
    <div v-for="(title, i) in titles" :key="`tabcontent-${i}`" class="tab-content" :style="`padding: 10px; ${active != i ? 'display: none;' : ''}`">
      <slot :name="`content-${i}`" style="padding: 10px;"/>
    </div>
  </div>
</div>
</template>
<script>
  export default {
    name: 'ela-tab',
    props: {
      titles: {
        type: Array,
        required: true
      }
    },
    data: () => ({
      active: 0
    })
  }
</script>
