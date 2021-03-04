export class DeathDto {

    private _uuid: string;
    private _name: string;
    private _reason: string;


    constructor(uuid: string, name: string, reason: string) {
        this._uuid = uuid;
        this._name = name;
        this._reason = reason;
    }

    get uuid(): string {
        return this._uuid;
    }

    set uuid(value: string) {
        this._uuid = value;
    }

    get name(): string {
        return this._name;
    }

    set name(value: string) {
        this._name = value;
    }

    get reason(): string {
        return this._reason;
    }

    set reason(value: string) {
        this._reason = value;
    }
}