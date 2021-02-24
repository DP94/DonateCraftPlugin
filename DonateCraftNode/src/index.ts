import express from 'express';
import {createConnection} from 'typeorm';
import {Death} from './entities/Death';
import bodyParser from 'body-parser';
import cors from 'cors';
import {DeathsDto} from "./dtos/deaths.dto";
import path from "path";

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
        username: "dan",
        password: "Suicune245",
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
        username: "dan",
        password: "Suicune245",
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
