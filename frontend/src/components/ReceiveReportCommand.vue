<template>
    <v-card outlined>
        <v-card-title>
            ReceiveReport
        </v-card-title>

        <v-card-text>
            <String label="ReportId" v-model="value.reportId" :editMode="editMode"/>
            <Address offline label="Location" v-model="value.location" :editMode="editMode" @change="change"/>
            <File offline label="Video" v-model="value.video" :editMode="editMode" @change="change"/>
            <Comment offline label="Description" v-model="value.description" :editMode="editMode" @change="change"/>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="primary"
                    text
                    @click="receiveReport"
            >
                ReceiveReport
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
        name: 'ReceiveReportCommand',
        components:{},
        props: {},
        data: () => ({
            editMode: true,
            value: {},
        }),
        created() {
            this.value.reportId = '';
            this.value.location = {};
            this.value.video = {};
            this.value.description = {};
        },
        watch: {
        },
        methods: {
            receiveReport() {
                this.$emit('receiveReport', this.value);
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

