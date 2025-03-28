<template>
    <div>
        <v-card
            class="mx-auto"
            outlined
            color="primary"
            style="padding:10px 0px 10px 0px; margin-bottom:40px;"
        >
            <v-row>
                <v-list-item class="d-flex">
                    <v-list-item-avatar
                        size="70"
                        class="border-color"
                    >
                    </v-list-item-avatar>
                    <h1 class="align-self-center ml-3">AI분석</h1>
                    <div class="secondary-text-color" style="margin-left:30px;">AI를 통한 위험 평가 및 대응 전략 제안을 관리하는 애그리거트</div>
                </v-list-item>
            </v-row>
        </v-card>
        <v-col style="margin-bottom:40px;">
            <div class="text-center">
                <v-dialog
                        v-model="openDialog"
                        width="332.5"
                        fullscreen
                        hide-overlay
                        transition="dialog-bottom-transition"
                >
                    <AianalysisAiAnalysis :offline="offline" class="video-card" :isNew="true" :editMode="true" v-model="newValue" 
                            @add="append" v-if="tick"/>

                    <v-btn
                            style="postition:absolute; top:2%; right:2%"
                            @click="closeDialog()"
                            depressed
                            icon 
                            absolute
                    >
                        <v-icon>mdi-close</v-icon>
                    </v-btn>
                </v-dialog>

                <v-row>
                    <v-card
                        class="mx-auto"
                        style="height:300px; width:300px; margin-bottom:20px; text-align: center;"
                        outlined
                    >
                        <v-list-item>
                            <v-list-item-avatar 
                                class="mx-auto"
                                size="80"
                                style="margin-top:80px;"
                            ><v-icon color="primary" x-large>mdi-plus</v-icon>
                            </v-list-item-avatar>
                        </v-list-item>

                        <v-card-actions>
                            <v-btn 
                                v-on="on"
                                class="mx-auto"
                                outlined
                                rounded
                                @click="openDialog=true;"
                                color="primary"
                                style="font-weight:500; font-size:20px; padding:15px; border:solid 2px; max-width:250px; overflow:hidden"
                            >
                                AI분석 등록
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-row>
            </div>
        </v-col>
        <v-row>
            <AianalysisAiAnalysis :offline="offline" class="video-card" v-for="(value, index) in values" v-model="values[index]" v-bind:key="index" @delete="remove"/>
        </v-row>
    </div>
</template>

<script>

    const axios = require('axios').default;
    import AianalysisAiAnalysis from './../AianalysisAiAnalysis.vue';

    export default {
        name: 'AianalysisAiAnalysisManager',
        components: {
            AianalysisAiAnalysis,
        },
        props: {
            offline: Boolean
        },
        data: () => ({
            values: [],
            newValue: {},
            tick : true,
            openDialog : false,
        }),
        async created() {
            var me = this;
            if(me.offline){
                if(!me.values) me.values = [];
                return;
            } 

            var temp = await axios.get(axios.fixUrl('/aiAnalyses'))
            me.values = temp.data._embedded.aiAnalyses;
            
            me.newValue = {
                'analysisId': '',
                'riskLevel': '',
                'resources': '',
                'response': '',
                'eventId': '',
                'status': '',
            }
        },
        methods:{
            closeDialog(){
                this.openDialog = false
            },
            append(value){
                this.tick = false
                this.newValue = {}
                this.values.push(value)
                
                this.$emit('input', this.values);

                this.$nextTick(function(){
                    this.tick=true
                })
            },
            remove(value){
                var where = -1;
                for(var i=0; i<this.values.length; i++){
                    if(this.values[i]._links.self.href == value._links.self.href){
                        where = i;
                        break;
                    }
                }

                if(where > -1){
                    this.values.splice(i, 1);
                    this.$emit('input', this.values);
                }
            },
        }
    };
</script>


<style>
    .video-card {
        width:300px; 
        margin-left:4.5%; 
        margin-top:50px; 
        margin-bottom:50px;
    }
</style>

