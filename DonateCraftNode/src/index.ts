import express from 'express';
import {createConnection} from 'typeorm';
import {Death} from './entities/Death';
import bodyParser from "body-parser";

const app = express();
const PORT = 8000;
const jsonParser = bodyParser.json();
app.listen(PORT, () => {
    console.log(`Server is running at http://localhost:${PORT}`);
});

app.post('/death', jsonParser, (request, res) => {
    const uuid = request.body.uuid;
    createConnection({
        type: "mysql",
        host: "localhost",
        port: 3306,
        username: "replace",
        password: "replace",
        database: "donatecraft",
        entities: [
            Death
        ],
        synchronize: true,
        logging: false
    }).then(async connection => {
        const deathRepository = connection.getRepository(Death);
        let death : Death | undefined = await deathRepository.findOne({uuid: uuid})
        if (death === undefined) {
            death = new Death();
            death.uuid = uuid;
            death.deathcount = 1;
        } else {
           death.deathcount++;
        }
        await connection.manager.save(death);
        await connection.close();
    }).catch(error => console.log(error));
});
