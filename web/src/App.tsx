import { Button, createStyles, CssBaseline, Input, InputLabel, Paper, Theme, withStyles } from '@material-ui/core'
import FormControl from '@material-ui/core/FormControl'
import React, { Component } from 'react'

const styles = ({ breakpoints, spacing }: Theme) => createStyles({
  form: {
    marginTop: spacing.unit,
    width: '100%',
  },
  main: {
    display: 'block',
    marginLeft: spacing.unit * 3,
    marginRight: spacing.unit * 3,
    [breakpoints.up(400 + spacing.unit * 3 * 2)]: {
      marginLeft: 'auto',
      marginRight: 'auto',
      width: 400,
    },
    width: 'auto',
  },
  paper: {
    alignItems: 'center',
    display: 'flex',
    flexDirection: 'column',
    marginTop: spacing.unit * 8,
    padding: `${spacing.unit * 2}px ${spacing.unit * 3}px ${spacing.unit * 3}px`,
  },
  submit: {
    marginTop: spacing.unit * 3,
  },
})

interface IProps {
  classes: {
    main: string,
    paper: string,
    form: string,
    submit: string,
  }
}

class App extends Component<IProps> {
  public render() {
    const { classes } = this.props

    return (
      <main className={classes.main}>
        <CssBaseline />
        <Paper className={classes.paper}>
          <form className={classes.form}>
            <FormControl margin="normal" required={true} fullWidth={true}>
              <InputLabel htmlFor="email">Email Address</InputLabel>
              <Input id="email" name="email" autoComplete="email" />
            </FormControl>
            <Button
              type="submit"
              fullWidth={true}
              variant="contained"
              color="primary"
              className={classes.submit}
            >
              Subscribe
            </Button>
          </form>
        </Paper>
      </main>
    )
  }
}

export default withStyles(styles)(App)
