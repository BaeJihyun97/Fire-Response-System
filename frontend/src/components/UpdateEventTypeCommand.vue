<template>
    <v-card outlined>
        <v-card-title>
            UpdateEventType
        </v-card-title>

        <v-card-text>
            <String label="EventId" v-model="value.eventId" :editMode="editMode"/>
            <String label="EventType" v-model="value.eventType" :editMode="editMode"/>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="primary"
                    text
                    @click="updateEventType"
            >
                UpdateEventType
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
        name: 'UpdateEventTypeCommand',
        components:{},
        props: {},
        data: () => ({
            editMode: true,
            value: {},
        }),
        created() {
            this.value.eventId = '';
            this.value.eventType = '';
        },
        watch: {
        },
        methods: {
            updateEventType() {
                this.$emit('updateEventType', this.value);
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

