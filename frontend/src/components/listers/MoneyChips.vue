<template>
    <div style="margin: 0 -15px 0 -15px;">
        <v-list two-line v-if="value.length > 0">
            <v-list-group v-model="isExpansion">
                <template v-slot:activator>
                    <v-list-item-content>
                        <v-list-item-title>Money</v-list-item-title>
                    </v-list-item-content>
                </template>
            
                <v-list-item v-for="(item, idx) in value" :key="idx">
                    <v-list-item-content>
                        <v-list-item-title>Message {{idx+1}}</v-list-item-title>
                        <v-list-item-subtitle>
                            Amount :  {{item.amount }} <br>
                            Currency :  {{item.currency }} <br>
                        </v-list-item-subtitle>
                    </v-list-item-content>
                    <v-list-item-action v-if="editMode">
                        <v-btn icon @click="deleteItem(item)">
                            <v-icon small color="secondary lighten-1">mdi-delete</v-icon>
                        </v-btn>
                    </v-list-item-action>
                </v-list-item>
            </v-list-group>
        </v-list>

        <Money v-if="editMode && tick" offline :isNew="true" :inList="true"
                :editMode="editMode" v-model="newValue" @add="append">
            <template slot="actions">
                <v-spacer></v-spacer>
                <v-btn
                        color="primary"
                        text
                        @click="edit"
                        v-if="!editMode"
                >
                    Edit
                </v-btn>
                
                <v-btn
                        color="primary"
                        text
                        @click="append(newValue)"
                        v-else
                >
                    Add
                </v-btn>
                <v-btn
                        color="primary"
                        text
                        @click="remove"
                        v-if="!editMode"
                >
                    Delete
                </v-btn>
            </template>
        </Money>
    </div>
</template>

<script>
    import Money from '../Money.vue';

    export default {
        name: 'MoneyManager',
        components: {
            Money
        },
        props: {
            value: [Object, String, Number, Boolean, Array],
            offline: Boolean,
            editMode: Boolean
        },
        data: () => ({
            newValue: {},
            tick : true,
            isExpansion: false,
        }),
        async created() {
            if (!this.value) {
                this.value = [];
            }
        },
        methods: {
            append(value){
                this.tick = false
                this.newValue = {}

                if(!this.value){
                    this.value = [];
                }

                this.value.push(value)
                
                this.$emit('input', this.value);

                this.$nextTick(function(){
                    this.tick=true
                })
            },
            remove(value) {
                var where = -1;
                for (var i=0; i<this.value.length; i++) {
                    if(this.value[i]._links.self.href == value._links.self.href) {
                        where = i;
                        break;
                    }
                }

                if(where > -1) {
                    this.value.splice(i, 1);
                    this.$emit('input', this.value);
                }
            },
            deleteItem(val) {
                this.value.splice(this.value.indexOf(val), 1);
                this.$emit('input', this.value);
            },
        }
    };
    
</script>

