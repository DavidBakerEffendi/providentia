export interface IDatabase {
    id?: String;
    name?: String;
    description?: String;
    icon?: String;
}

export class Dataset implements IDatabase {

    constructor(
        public id?: String,
        public name?: String,
        public description?: String,
        public icon?: String,
    ) {}

}
