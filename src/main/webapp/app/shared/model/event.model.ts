import { IRecord } from 'app/shared/model//record.model';

export interface IEvent {
    id?: number;
    name?: string;
    codes?: IRecord[];
}

export class Event implements IEvent {
    constructor(public id?: number, public name?: string, public codes?: IRecord[]) {}
}
