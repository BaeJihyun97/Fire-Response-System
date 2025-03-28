<template>
    <div style="margin: 0 -15px 0 -15px;" v-if="editMode">
        <v-card-title>
            {{label}}
        </v-card-title>
        <vue-tags-input
            style="margin-left: 15px;"
            v-model="tag"
            :tags="copyTags"
            :autocomplete-items="filteredItems"
            @tags-changed="newTags => copyTags = newTags"
        />
        <v-chip
        style="background-color: #5C6BC0; color: #fff; border-radius: 2px; padding: 3px 5px; margin: 2px; font-size: .85em; height:30px;"
        v-model="value.tag"
        v-for="(tag, index) in value.tag" :key="index"
        >
            {{ tag }}
            <v-icon  @click="deleteTag(index)" style="font-size: 15px; right: -5px; top: -8px;">mdi-close</v-icon>
        </v-chip>
    </div>
    <div v-else>
        <v-chip
        style="background-color: #5C6BC0; color: #fff; border-radius: 2px; padding: 3px 5px; margin: 2px; font-size: .85em; height:30px;"
        v-model="value.tag"
        v-for="(tag, index) in value.tag" :key="index"
        >
            {{ tag }}
        </v-chip>
    </div>
</template>

<script>
import VueTagsInput from '@johmun/vue-tags-input';

export default {
    name: "Tags",
    components: {
        VueTagsInput,
    },
    data() {
        return {
            tag: '',
            copyTags:[],
            autocompleteItems: [],
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
                tag: [],
            };
        }
    },
    watch: {
        copyTags(newVal){
            if(!this.value.tag){
                this.value.tag = []
            }
            if(newVal){
                newVal.forEach(tag => {
                    if (tag.text && !this.value.tag.includes(tag.text)) {
                        this.value.tag.push(tag.text);
                    }
                });
            }
        }
    },
    computed: {
        filteredItems() {
            return this.autocompleteItems.filter(item => {
                return item.text.toLowerCase().indexOf(this.tag.toLowerCase()) !== -1;
            });
        },
    },

    methods: {
        deleteTag(index) {
            this.value.tag.splice(index, 1);
        }
    }
}
</script>