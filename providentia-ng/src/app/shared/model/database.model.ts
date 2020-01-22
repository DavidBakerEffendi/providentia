export interface IDatabase {
    database_id?: string;
    name?: string;
    description?: string;
    icon?: string;
    status?: string;
}

export class Database implements IDatabase {

    constructor(
        public database_id?: string,
        public name?: string,
        public description?: string,
        public icon?: string,
        public status?: string
    ) {}

}
