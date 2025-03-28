<template>
    <div style="margin: 15px 0 0 -15px;">
        <v-card-title>
        </v-card-title>
        <v-card
            v-if="!editMode"
            class="mx-auto"
            max-width="400"
            style="box-shadow: none;"
        >
            <v-row
                align="end"
                class="fill-height"
            >
                <v-col
                    align-self="start"
                    class="pa-0"
                    cols="12"
                >
                </v-col>
                <v-col class="py-0">
                    <v-list-item color="rgba(0, 0, 0, .4)" style="padding: 0px;">
                        <v-col >
                            <v-list-item-content v-for="(comment, index) in value.comment" :key="index" style="padding:0px;">
                                <v-list-item-title class="text-h6" >
                                    <v-avatar color="white">
                                        <v-img
                                            :src="value.profileImg ? value.profileImg:'https://user-images.githubusercontent.com/92732781/174538537-631a0ee2-40bb-4589-a58b-67da0ef17e38.png'"
                                            class="mx-auto"
                                        ></v-img>
                                    </v-avatar>
                                    user{{index + 1}}
                                </v-list-item-title>
                                <v-list-item-subtitle v-if="!editCommentMode" style="font-weight:500; padding:0px; margin-left: 7%;">
                                    {{ comment }}
                                    <v-icon v-if="!editCommentMode" @click="editComment(comment, index)" style="font-size: 15px; ">mdi-pencil</v-icon>
                                    <v-icon @click="deleteComment(index)" style="font-size: 15px;">mdi-close</v-icon>
                                </v-list-item-subtitle>
                                <v-list-item-subtitle v-else-if="editCommentMode && selectedComment === comment" style="font-weight:500; padding:0px;">
                                    <v-row>
                                        <v-col cols="10">
                                            <v-text-field v-model="inputComment" outlined solo></v-text-field>
                                        </v-col>
                                        <v-col cols="2">
                                            <v-icon @click="saveComment(inputComment, index)" style="font-size: 20px; margin-right: 10px;">mdi-content-save</v-icon>
                                        </v-col>
                                    </v-row>
                                </v-list-item-subtitle>
                            </v-list-item-content>
                        </v-col>
                    </v-list-item>
                    <div v-if="!editCommentMode" style="display: flex; align-items: center;">
                        <v-text-field v-model="inputComment" solo class="comment-box" @keydown.enter="addComment(inputComment)"></v-text-field>
                    </div>
                </v-col>
            </v-row>
        </v-card>
    </div>
</template>

<script>
export default {
    name: "Comment",
    data() {
        return {
            inputComment: "",
            previousComment: "",
            editCommentMode: false,
            selectedComment: null,
        };
    },
    props: {
        editMode: Boolean,
        value: Object,
        label: String,
    },
    created() {
        if (!this.value) {
            this.value = {
                comment: [],
            };
        }
    },
    watch: {
        value(newVal) {
            this.$emit("input", newVal);
        },
    },
    methods: {
        addComment(inputComment) {
            if (!this.value.comment) {
                this.value.comment = [];
            }
            // 이중 입력 방지
            if (this.inputComment === "") {
                return;
            }
            if (
                this.previousComment &&
                this.previousComment.slice(-1) === this.inputComment.charAt(0)
            ) {
                this.inputComment = "";
                return;
            }
            this.previousComment += this.inputComment;
            this.value.comment.push(inputComment);
            this.inputComment = "";
        },
        editComment(comment, index) {
            this.editCommentMode = true;
            this.selectedComment = comment
            this.inputComment = this.value.comment[index]
        },
        saveComment(inputComment, index) {
            this.editCommentMode = false;
            this.value.comment.splice(index, 1);
            if(!this.value.comment[index]){
                this.value.comment[index] = inputComment
            }
            this.inputComment = "";
        },
        deleteComment(index) {
            this.value.comment.splice(index, 1);
        },
    },
};
</script>

<style>
.address-v-card-title {
    display: contents;
}

.address-v-text-field {
    margin-top: 5px;
}

.comment-box .v-input__control .v-text-field__details {
    margin-bottom: -20px;
}
</style>