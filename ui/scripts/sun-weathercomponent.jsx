import React from 'react'
import axios from 'axios'

class SunWeatherComponent extends React.Component {
    constructor(props){
        super(props)
        this.state = {
            sunrise: null,
            sunset: null,
            temperature: null,
            cityName: null,
            requestsCount: null,
        }
    }

    componentDidMount(){
        axios.get('/api/data').then(resp => {
            const json = resp.data
            this.setState({
                sunrise: json.sunInfo.sunrise,
                sunset: json.sunInfo.sunset,
                temperature: json.temperature,
                cityName: json.sunInfo.city,
                requestsCount: json.requestsCount,
            })
        }).catch(err => console.error(err))
    }

    render() {
        return <table>
            <tbody>
                <tr>
                    <td>City name</td>
                    <td>{this.state.cityName}</td>
                </tr>
                <tr>
                    <td>Sunrise time</td>
                    <td>{this.state.sunrise}</td>
                </tr>
                <tr>
                    <td>Sunset time</td>
                    <td>{this.state.sunset}</td>
                </tr>
                <tr>
                    <td>Current temperature</td>
                    <td>{this.state.temperature}</td>
                </tr>
                <tr>
                    <td>Requests</td>
                    <td>{this.state.requestsCount}</td>
                </tr>
            </tbody>
        </table>
    }
}

export default SunWeatherComponent