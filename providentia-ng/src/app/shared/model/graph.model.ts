export interface IGraph {
    id?: String;
    result_id?: String;
    graphson?: String;
}

export class Graph implements IGraph {

    constructor(
        public id?: String,
        public result_id?: String,
        public graphson?: String,
    ) {}

}
