import axios from 'axios'
import { UserAgentApplication } from 'msal'

export default class GraphClient {
  private config = {
    auth: {
      clientId: 'd302c27c-4dba-42ae-8ac8-d595967e9e7d',
      redirectUri: 'http://localhost:8080'
    }
  }

  private userAgentApplication: UserAgentApplication

  private loginRequest = {
    scopes: ['https://graph.microsoft.com/User.Read']
  }

  private accessTokenRequest = {
    scopes: ['user.read']
  }

  constructor() {
    this.userAgentApplication = new UserAgentApplication(this.config)
  }

  public login() {
    return new Promise((resolve, reject) => {
      this.userAgentApplication.loginPopup(this.loginRequest)
        .then(response => {
          resolve()
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  public getAccessToken() {
    return new Promise((resolve, reject) => {
      this.userAgentApplication.acquireTokenSilent(this.accessTokenRequest)
        .then(response => {
          resolve(response.accessToken)
        })
        .catch(error => {
          if (error.errorMessage.indexOf('interaction_required') !== -1) {
            this.userAgentApplication.acquireTokenPopup(this.accessTokenRequest)
              .then(response => {
                resolve(response.accessToken)
              })
              .catch(accessTokenError => {
                reject(accessTokenError)
              })
          } else {
            reject(error)
          }
        })
    })
  }

  public getMe() {
    return new Promise((resolve, reject) => {
      this.getAccessToken()
        .then(accessToken => {
          axios.get('https://graph.microsoft.com/v1.0/me', {
            headers: {
              'Authorization': `Bearer ${accessToken}`
            }
          })
            .then(response => {
              resolve(response.data)
            })
            .catch(error => {
              reject(error)
            })
        })
        .catch(error => {
          reject(error)
        })
    })
  }
}
