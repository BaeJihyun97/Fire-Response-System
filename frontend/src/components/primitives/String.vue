<template>
    <div>
        <div v-if="editMode" style="margin-top:-20px;">
            <v-textarea v-if="multiLine"
                    :label="label" 
                    v-model="value"
                    @change="change"
            />
            <v-text-field v-else
                    :label="label" 
                    v-model="value"
                    @change="change"
            />
        </div>
        <div v-else>
            {{label}} :  {{value}}
        </div>
    </div>
</template>

<script>  
    export default {
        name: 'String',
        components:{
        },
        props: {
            value:{
                type: String,
                default: ''
            },
            editMode: Boolean,
            label: String,
            inputUI: String
        },
        computed: {
            multiLine() {
                return this.inputUI === 'TEXTAREA';
            }
        },
        methods:{
            change(){
                this.$emit("input", this.value);
            }
        }
    }
</script>
