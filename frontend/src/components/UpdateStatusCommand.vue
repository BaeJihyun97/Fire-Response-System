<template>
    <v-card outlined>
        <v-card-title>
            UpdateStatus
        </v-card-title>

        <v-card-text>
            <String label="EventId" v-model="value.eventId" :editMode="editMode"/>
            <String label="Status" v-model="value.status" :editMode="editMode"/>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="primary"
                    text
                    @click="updateStatus"
            >
                UpdateStatus
            </v-btn>
            
            <v-btn
                    color="primary"
                    text
                    @click="close"
            >
                Close
            </v-btn>
        </v-card-actions>
    </v-card>

</template>

<script>
   
    export default {
        name: 'UpdateStatusCommand',
        components:{},
        props: {},
        data: () => ({
            editMode: true,
            value: {},
        }),
        created() {
            this.value.eventId = '';
            this.value.status = '';
        },
        watch: {
        },
        methods: {
            updateStatus() {
                this.$emit('updateStatus', this.value);
            },
            close() {
                this.$emit('closeDialog');
            },
            change() {
                this.$emit('input', this.value);
            },
        }
    }
</script>

