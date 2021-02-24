import express from 'express';
import {createConnection} from 'typeorm';
import {Death} from './entities/Death';
import bodyParser from 'body-parser';
import cors from 'cors';
import {DeathsDto} from "./dtos/deaths.dto";
import path from "path";
require('dotenv').config()

const DC_DB_USERNAME = process.env.DC_DB_USERNAME;
const DC_DB_PASSWORD = process.env.DC_DB_PASSWORD;
if(!DC_DB_USERNAME) {
  console.error("DC_DB_USERNAME not set");
  process.exit(-1);
}
if(!DC_DB_PASSWORD) {
  console.error("DC_DB_PASSWORD not set");
  process.exit(-1);
}

const app = express();
const PORT = 8000;
const jsonParser = bodyParser.json();
app.use(cors());
app.use(express.static(process.cwd() + "/build/public/"));
app.listen(PORT, () => {
    console.log(`Server is running at http://localhost:${PORT}`);
});

app.get('/', (request, response) => {
    response.sendFile(path.resolve(__dirname, 'build', 'index.html'));
});

app.post('/death', jsonParser, (request, res) => {
    const uuid = request.body.uuid;
    const name = request.body.name;
    createConnection({
        type: "mysql",
        host: "localhost",
        port: 3306,
        username: DC_DB_USERNAME,
        password: DC_DB_PASSWORD,
        database: "donatecraft",
        entities: [
            Death
        ],
        synchronize: true,
        logging: false
    }).then(async connection => {
        const deathRepository = connection.getRepository(Death);
        let death: Death | undefined = await deathRepository.findOne({uuid: uuid})
        if (death === undefined) {
            death = new Death();
            death.uuid = uuid;
            death.deathcount = 1;
            death.name = name;
        } else {
            death.name = name;
            death.deathcount++;
        }
        await connection.manager.save(death);
        await connection.close();
    }).catch(error => console.log(error));
});

app.get('/deaths', jsonParser, (request, response) => {
    createConnection({
        type: "mysql",
        host: "localhost",
        port: 3306,
        username: DC_DB_USERNAME,
        password: DC_DB_PASSWORD,
        database: "donatecraft",
        entities: [
            Death
        ],
        synchronize: true,
        logging: false
    }).then(async connection => {
        const deathRepository = connection.getRepository(Death);
        const deaths: Death[] = await deathRepository.find();
        const deathDTO: DeathsDto = new DeathsDto();
        deathDTO.deaths = deaths;
        await connection.close();
        response.setHeader('Content-Type', 'application/json');
        response.end(JSON.stringify(deathDTO));
    }).catch(error => console.log(error));
});
