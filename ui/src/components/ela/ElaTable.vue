<template>
  <div class="table-responsive">
    <table :class="`table ${onSelect ? 'table-hover' : ''}`">
      <thead>
      <tr>
        <th v-for="(h, i) in headers" :key="`th-${i}`">{{h.text}}</th>
      </tr>
      </thead>
      <tbody>
      <tr @click="onSelect(dataRow)" v-for="(dataRow, i) in items" :key="`dr-${i}`">
        <th v-for="(h, j) in headers" :key="`td-${i}-${j}`" >{{dataRow[h.value]}}</th>
      </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
  export default {
    name: 'ela-table',
    props: {
      headers: {
        type: Array,
        default: () => null
      },
      items: {
        type: Array,
        default: () => []
      },
      onSelect: {
        type: Function,
        default: null
      }
    }
  }
</script>
