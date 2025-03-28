<template>
    <v-card outlined>
        <v-card-title>
            RequestAiAnalysis
        </v-card-title>

        <v-card-text>
            <String label="AnalysisId" v-model="value.analysisId" :editMode="editMode"/>
            <Rating offline label="RiskLevel" v-model="value.riskLevel" :editMode="editMode" @change="change"/>
            <Money offline label="Resources" v-model="value.resources" :editMode="editMode" @change="change"/>
            <Comment offline label="Strategy" v-model="value.strategy" :editMode="editMode" @change="change"/>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="primary"
                    text
                    @click="requestAiAnalysis"
            >
                RequestAiAnalysis
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
        name: 'RequestAiAnalysisCommand',
        components:{},
        props: {},
        data: () => ({
            editMode: true,
            value: {},
        }),
        created() {
            this.value.analysisId = '';
            this.value.riskLevel = {};
            this.value.resources = {};
            this.value.strategy = {};
        },
        watch: {
        },
        methods: {
            requestAiAnalysis() {
                this.$emit('requestAiAnalysis', this.value);
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

