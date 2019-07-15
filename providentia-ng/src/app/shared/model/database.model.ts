export interface IDatabase {
    id?: String;
    name?: String;
    description?: String;
    icon?: String;
    status?: String;
}

export class Database implements IDatabase {

    constructor(
        public id?: String,
        public name?: String,
        public description?: String,
        public icon?: String,
        public status?: String
    ) {}

}
