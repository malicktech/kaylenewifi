import { IEvent } from 'app/shared/model//event.model';

export interface IRecord {
    id?: number;
    phone?: string;
    code?: string;
    event?: IEvent;
}

export class Record implements IRecord {
    constructor(public id?: number, public phone?: string, public code?: string, public event?: IEvent) {}
}
