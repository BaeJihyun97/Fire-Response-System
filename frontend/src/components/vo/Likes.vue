<template>
    <div style="margin: 0 -15px 0 -15px;">
        <div v-if="!editMode" style="margin-top: 0px;">
            <v-icon v-if="!value.isLiked" v-model="value.isLiked" @click="clickLike()" style="color:black; margin-left: 15px;">mdi-heart-outline</v-icon>
            <v-icon v-else v-model="value.isLiked" @click="clickLike()" style="color: red; margin-left: 15px;" >mdi-heart</v-icon>
        </div>
    </div>
</template>

<script>
    
    export default {
        name:"Likes",
        props: {
            editMode: Boolean,
            value : Object,
            label : String,
        },
        created(){
            if(!this.value) {
                    this.value = {
                    'isLiked': false,
                }
            }
        },
        watch: {
            value(newVal) {
                this.$emit('input', newVal);
            },
        },
        methods: {
            clickLike(){
                this.value.isLiked = !this.value.isLiked
            }
        }
    }
</script>

<style>
</style>

